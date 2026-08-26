from __future__ import print_function
import sys

# The common module provides setup functionality used by the samples,
# such as authentication and unique id generation.
from shopping.content import common
offer_id = 'book#%s' % common.get_unique_id()
product = {
     'offerId':
         offer_id,
     'title':
         'A Tale of Two Cities',
     'description':
         'A classic novel about the French Revolution',
     'link':
         'http://my-book-shop.com/tale-of-two-cities.html',
     'imageLink':
         'http://my-book-shop.com/tale-of-two-cities.jpg',
     'contentLanguage':
         'en',
     'targetCountry':
         'US',
     'channel':
         'online',
     'availability':
         'in stock',
     'condition':
         'new',
     'googleProductCategory':
         'Media > Books',
     'gtin':
         '9780007350896',
     'price': {
         'value': '2.50',
         'currency': 'USD'
     },
     'shipping': [{
         'country': 'US',
         'service': 'Standard shipping',
         'price': {
             'value': '0.99',
             'currency': 'USD'
         }
     }],
     'shippingWeight': {
         'value': '200',
         'unit': 'grams'
     }
}
