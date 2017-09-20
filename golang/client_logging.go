package main

import (
	"fmt"
	"io"
	"log"
	"net/http"
	"net/http/httputil"
	"sync"
)

type loggedRoundTripper struct {
	Output      io.Writer
	OutputMutex *sync.Mutex
	Delegate    http.RoundTripper
}

func (lrt loggedRoundTripper) dumpLogs(reqBytes []byte, respBytes []byte) error {
	lrt.OutputMutex.Lock()
	defer lrt.OutputMutex.Unlock()

	if _, err := lrt.Output.Write(reqBytes); err != nil {
		return fmt.Errorf("error logging request: %v", err)
	}
	if _, err := lrt.Output.Write(respBytes); err != nil {
		return fmt.Errorf("error logging response: %v", err)
	}

	return nil
}

// RoundTrip logs the outgoing HTTP request, delegates sending the request to
// the wrapped RoundTripper, and logs the incoming HTTP response, if any.
func (lrt loggedRoundTripper) RoundTrip(req *http.Request) (*http.Response, error) {
	reqBytes, err := httputil.DumpRequest(req, true)
	if err != nil {
		req.Body.Close() // RoundTrippers must close the request body even in error cases.
		return nil, fmt.Errorf("error dumping request in logger: %v", err)
	}

	resp, err := lrt.Delegate.RoundTrip(req)
	if err != nil {
		// Since we won't be receiving a response, attempt to write the request (since we
		// already have a pending error, don't worry about checking whether it succeeds).
		lrt.dumpLogs(reqBytes, nil)
		return resp, err
	}

	respBytes, err := httputil.DumpResponse(resp, true)
	if err != nil {
		log.Printf("error dumping response in logger: %v", err)
		lrt.dumpLogs(reqBytes, nil)
		return resp, nil
	}

	// Write the request and responses to disk within the same lock to avoid unwanted interleaving
	// if this RoundTripper is used concurrently within multiple goroutines.
	if err := lrt.dumpLogs(reqBytes, respBytes); err != nil {
		log.Print(err.Error())
	}

	return resp, nil
}

func logClient(client *http.Client, writer io.Writer) {
	client.Transport = loggedRoundTripper{
		Output:      writer,
		OutputMutex: &sync.Mutex{},
		Delegate:    client.Transport,
	}
}
