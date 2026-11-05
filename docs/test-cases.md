# Automated test cases

| Test case ID | Title | Steps | Expected result | Priority | Tags |
| --- | --- | --- | --- | --- | --- |
| UI-001 | Valid user can log in | Create a unique practice account by API; open Login; enter its email/password; submit. | Navigation shows the logged-in account. | P1 | ui, smoke, regression |
| UI-002 | Invalid credentials show a login error | Open Login; enter an unregistered email and incorrect password; submit. | Login remains rejected and shows an incorrect-credentials message. | P1 | ui, regression |
| UI-003 | User can register a new account | Open Signup/Login; enter a unique name/email; complete required account and address fields; create the account. | Account-created confirmation is shown and the user is logged in. | P1 | ui, smoke, regression |
| UI-004 | Duplicate email is rejected | Create an account by API; try to sign up again in the UI with the same email. | Signup displays the duplicate-email validation message. | P1 | ui, regression |
| UI-005 | Empty password is rejected during registration | Start a unique signup; complete account fields but leave password empty; submit. | Browser form validation marks the required password invalid and blocks registration. | P1 | ui, regression |
| UI-006 | Product search returns matching items | Open Products; search for Blue Top. | Search results are shown and each result matches the query. | P1 | ui, smoke, regression |
| UI-007 | Category filter shows the selected category | Open Products; expand Women; select Dress. | The resulting category page shows the selected Women category. | P2 | ui, regression |
| UI-008 | Product detail page shows product information | Open Products; select the first product. | Product name, price, and category information are visible. | P1 | ui, regression |
| UI-009 | User can add a product to the cart | Open a product detail page; add one item; open the cart. | The cart lists one product. | P1 | ui, smoke, regression |
| UI-010 | User can remove a product from the cart | Add a product; open the cart; remove it. | The product row is removed and the cart is empty. | P2 | ui, regression |
| UI-011 | Cart quantity matches the selected quantity | Open a product; set quantity to four; add it; open the cart. | The cart quantity is four. | P1 | ui, regression |
| UI-012 | Checkout shows the registered address | Create a unique account by API; log in; add a product; proceed to checkout. | Delivery and billing details include the registered address and postal code. | P1 | ui, regression |
| UI-013 | User can complete checkout | Create and sign in to a unique account; add a product; proceed; enter practice card values; submit payment. | The order placed confirmation is displayed. | P1 | ui, smoke, regression |
| UI-014 | Payment form rejects missing fields | Create and sign in to a unique account; add a product; proceed to payment; submit with card fields empty. | Required payment fields remain invalid and payment is not submitted. | P1 | ui, regression |
| API-001 | Get product catalogue | Send GET to /productsList. | HTTP 200, API response code 200, and a non-empty products array. | P1 | api, smoke, regression |
| API-002 | Search products | Send POST to /searchProduct with search_product=top. | HTTP 200, API response code 200, and a non-empty products array. | P1 | api, regression |
| API-003 | Create user account | POST a unique account to /createAccount; delete it during cleanup. | HTTP 200 and API response code 201. | P1 | api, smoke, regression |
| API-004 | Get user account details | Create a unique account in this test; GET /getUserDetailByEmail; delete the account. | HTTP 200, API response code 200, and returned email/name match the test account. | P1 | api, regression |
| API-005 | Update user account | Create a unique account; PUT an updated name to /updateAccount; GET the account; delete it. | Update and read succeed and the new name is returned. | P1 | api, regression |
| API-006 | Delete user account | Create a unique account; DELETE /deleteAccount; look up the email. | Delete succeeds and the later lookup reports the account missing. | P1 | api, regression |
| API-007 | Reject account update without valid credentials | PUT an update for a non-existent email with an invalid password. | API response code 404 and an account-not-found message. | P1 | api, smoke, regression |
| DB-001 | MySQL connection and read-only health query | Configure MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD; run the db group. | The connection succeeds and the configured read-only query returns 1. | P2 | db, regression |

The API cases run independently. Tests that need an account create a unique record and clean it up in the same test. DB-001 is skipped when MySQL configuration is absent.
