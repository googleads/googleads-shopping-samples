package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"io/ioutil"
	"net/http"
)

type loggedRoundTripper struct {
	Output   *json.Encoder
	Delegate http.RoundTripper
}

type loggedRequest struct {
	Method string `json:"method"`
	URL    string `json:"url"`
	// Only one of the following fields should be set:
	// ParsedBody, if the body was a single JSON value
	// RawBody, otherwise.
	ParsedBody interface{} `json:"parsedBody,omitempty"`
	RawBody    []byte      `json:"rawBody,omitempty"`
}

type loggedResponse struct {
	StatusCode int `json:"statusCode"`
	// Only one of the following fields should be set:
	// ParsedBody, if the body was a single JSON value
	// RawBody, otherwise.
	ParsedBody interface{} `json:"parsedBody,omitempty"`
	RawBody    []byte      `json:"rawBody,omitempty"`
}

func (lrt loggedRoundTripper) RoundTrip(req *http.Request) (*http.Response, error) {
	loggedRequest := loggedRequest{
		Method: req.Method,
		URL:    req.URL.String(),
	}
	if req.Body != nil {
		copy, err := req.GetBody()
		if err != nil {
			return nil, fmt.Errorf("error copying request body in logger: %s", err.Error())
		}
		buf, err := ioutil.ReadAll(copy)
		if err != nil {
			return nil, fmt.Errorf("error reading request body in logger: %s", err.Error())
		}
		dec := json.NewDecoder(bytes.NewReader(buf))
		if err := dec.Decode(&loggedRequest.ParsedBody); err != nil {
			// Non-JSON contents, so just store the raw body instead.
			loggedRequest.RawBody = buf
		} else if dec.More() {
			// More than just a single JSON response, so again, store the raw body.
			loggedRequest.RawBody = buf
			loggedRequest.ParsedBody = nil
		}

	}
	if err := lrt.Output.Encode(loggedRequest); err != nil {
		return nil, fmt.Errorf("error encoding request as JSON in logger: %s", err.Error())
	}

	resp, err := lrt.Delegate.RoundTrip(req)
	if err != nil {
		return resp, err
	}

	loggedResponse := loggedResponse{
		StatusCode: resp.StatusCode,
	}
	if resp.ContentLength != 0 {
		oldBody := resp.Body
		defer oldBody.Close()
		contents, err := ioutil.ReadAll(oldBody)
		if err != nil {
			return resp, fmt.Errorf("error reading response body in logger: %s", err.Error())
		}
		buf := bytes.NewReader(contents)

		respDecoder := json.NewDecoder(buf)
		if err := respDecoder.Decode(&loggedResponse.ParsedBody); err != nil {
			// Non-JSON contents, so just store the raw body instead.
			loggedResponse.RawBody = contents
		} else if respDecoder.More() {
			// More than just a single JSON response, so again, store the raw body.
			loggedResponse.RawBody = contents
			loggedResponse.ParsedBody = nil
		}

		buf.Seek(0, 0)
		resp.Body = ioutil.NopCloser(buf)
	}
	if err := lrt.Output.Encode(loggedResponse); err != nil {
		return nil, fmt.Errorf("error encoding response as JSON in logger: %s", err.Error())
	}

	return resp, nil
}

func logClient(client *http.Client, writer io.Writer) {
	encoder := json.NewEncoder(writer)
	encoder.SetEscapeHTML(false)
	encoder.SetIndent("", "  ")
	client.Transport = loggedRoundTripper{
		Output:   encoder,
		Delegate: client.Transport,
	}
}
