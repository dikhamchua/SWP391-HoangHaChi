# API Standards

## Current API Architecture

API requests under `/api/*` are handled by `com.kiotretail.api.BaseController`, which maps exact request paths to `ApiAction` implementations.

Current known route:

- `/api/products` -> `GetProductsAction`

## API Response Shape

Use `ApiResponse` for standard responses:

- `status`: numeric status code
- `message`: human-readable message
- `data`: response payload

## Route Rules

- API routes must live under `/api`.
- Use plural nouns for collections, for example `/api/products`.
- Register new routes in `BaseController` until routing is refactored.
- Document every new route in this file.

## Parameter Rules

- Validate query parameters before calling DAOs.
- Clamp pagination limits to safe maximums.
- Default `page` to `1` when omitted or invalid.
- Default `limit` to `10` unless the endpoint documents otherwise.

## Error Rules

- Return consistent JSON error responses.
- Do not expose stack traces in response bodies.
- Use appropriate HTTP status codes on the servlet response.
- Log errors safely without sensitive request data.

## Current API Debt

- Error handling currently catches generic exceptions in `BaseController` and returns a basic system error message.
- Pagination parsing is local to `GetProductsAction` and should be standardized when additional endpoints are added.
- Content type formatting should be normalized to `application/json;charset=UTF-8`.
