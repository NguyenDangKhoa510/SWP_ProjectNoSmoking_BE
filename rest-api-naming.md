# REST API CONVENTIONS

---

reference: https://www.coursera.org/learn/apis

---

**1. Everything in lowercase, with hyphens and not abridged**

- The URI of your API should always be in lowercase, do not use camelCase, PascalCase or Title case
- Separate multiple words using hyphens, not underscore, not keep abridged, or shortened words

Example:

| URI                        | Status | Why                                                                                        |
|----------------------------|--------|--------------------------------------------------------------------------------------------|
| /customers                 | Good   | Plural and lowercase                                                                       |
| /customers/16/phone-number | Good   | Lowercase and hyphen used to separate words                                                |
| /customers/16/address/home | Good   | Lowercase, no trailing slash, displays the hierarchical relationship with forward slashes. | 
| /users/{userId}            | Good   | Variable in camelCase, and no hyphen in the variable name                                  |
| /Customers                 | Bad    | Title case                                                                                 |
| /generalMembers            | Bad    | camelCase, no hyphens to separate words                                                    |
| /MenuItems                 | Bad    | Pascal case                                                                                |
| /customers/16/tel-no       | Bad    | Abbreviation                                                                               |
| /customers/16/phone_number | Bad    | Underscores                                                                                |
| /customers/16/phonenumber  | Bad    | No separation of words                                                                     |
| /users/{user-id}           | Bad    | Variable should be in camelCase, and hyphen between words                                  |

**2. Use a forward slash to indicate a hierarchical relationship**

Example:

A store can have customers who have placed many orders and each of these orders can have delivery addresses, menu items
and bills.

| URI                                  | Status |
   |--------------------------------------|--------|
| /store/customers/{customerId}/orders | Good   |   
| /store/orders/{orderId}              | Good   |   
| /store/orders/{orderId}/menu-items   | Good   |   

**3. Use nouns for resource names, not verbs**

- Using noun to indicate resources, not verb
- Plural nouns for collection

Example

| URI               | Expects         | Status | Why                                                                    |
|-------------------|-----------------|--------|------------------------------------------------------------------------|
| /orders           | Collection      | Good   | Uses a noun, not a verb                                                |
| /users/{userId}   | Single user     | Good   | Uses a noun and proper hierarchical relationship and naming convention |
| /order            | Collection      | Bad    | Uses plural nouns for collections                                      |
| /getOrder         | Single resource | Bad    | Uses a verb, camelCase                                                 |
| /getUser/{userId} | Single user     | Bad    | Uses a verb, camelCase                                                 |

**4. Avoid special characters**

| URI                       | Status |
|---------------------------|--------|
| /users/12\|23\|23/address | Bad    |
| /orders/16/menu^items     | Bad    |
| /users/12,23,23/address   | Good   |

**5. Avoid file extensions in URI**

| URI                                           | Status |
|-----------------------------------------------|--------|
| /sports/basketball/teams/{teamId}.json        | Bad    |
| /sports/basketball/teams/{teamId}.xml         | Bad    |
| /sports/basketball/teams/{teamId}?format=json | Good   |
| /sports/basketball/teams/{teamId}?format=xml  | Good   |

**6. Use query parameters to filter when necessary**

| URI                                   | Status | Why                                       |
|---------------------------------------|--------|-------------------------------------------|
| /users/{userId}/locations             | Good   | Hierarchical                              |
| /users/{userId}/locations?country=USA | Good   | camelCase, no separation of words         |
| /articles?per-page=10&page=2          | Good   | Proper use of query string                |
| /users/{userId}/locations/USA         | Bad    | Doesn't use a query string to filter data |

**7. No trailing slash at endpoint.**

| URI                           | Status | Why               |
|-------------------------------|--------|-------------------|
| /users/{userId}               | Good   | No trailing slash |
| /articles/{articleId}/author  | Good   | No trailing slash |
| /users/{userId}/              | Bad    | Trailing slash    |
| /articles/{articleId}/author/ | Bad    | Trailing slash    |
