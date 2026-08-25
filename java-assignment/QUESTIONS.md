# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**

I would standardize the persistence strategy. The code base currently mixes different approaches for data access and entity manipulation. Maintaining a single repository pattern across all domains would improve consistency, readability, testability, and onboarding of new developers.

Business logic should remain in use cases/services while repositories should focus exclusively on persistence concerns. This separation simplifies unit testing and reduces coupling between domain logic and infrastructure.

---

2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**

OpenAPI-generated Warehouse API vs manually coded APIs

OpenAPI Pros

- Contract-first development
- Better collaboration with external consumers
- Client SDK generation
- Improved documentation consistency

OpenAPI Cons

- Additional generation complexity
- DTO mapping overhead
- Changes require specification updates

Manual API Pros

- Faster for small applications
- Less boilerplate

Manual API Cons

- Risk of documentation drift
- Harder contract governance
- Less consistency

My Choice

For external or business-critical APIs I prefer OpenAPI contract-first development. Internal applications with limited scope can start with manually coded endpoints, but long-term I would favor OpenAPI to ensure consistent contracts and documentation

---

3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**

I would prioritize testing according to business risk.

1. Use Case tests (highest priority)
2. Repository integration tests
3. REST endpoint tests
4. End-to-end scenarios

The business validations around warehouse creation, replacement, capacity management and location restrictions are the most critical and therefore deserve the largest testing investment.

To maintain effective coverage over time:

- In the current implementation, unit tests were added first for `LocationGateway` and Warehouse use cases (positive and negative paths).
- JaCoCo report generation is configured, and an 80% coverage threshold is enforced for the assignment core packages (location and warehouse use cases).
- CI runs `mvn verify`, so tests and the coverage gate are checked on every PR.
- For future changes, I would require tests for every new business rule before merge.
