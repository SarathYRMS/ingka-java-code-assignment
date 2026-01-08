# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
Yes, I would consider refactoring the package structure, database access layer and few other clean code strategies 
to ensure consistency and maintainability. 
Currently, the code base uses a mix of direct JPQL queries 
(e.g., `list("archivedAt is null")`) and repository methods (e.g., `findByWarehouseId`). 
This inconsistency can lead to confusion and make the code harder to maintain.

Refactoring could involve:
1. Refactoring the package structure to maintain consistency across modules with proper alignment of adapters, domain and mappers
2. Standardizing the use of a repository pattern with clearly defined methods for all database operations. 
This improves readability, testing, and adherence to the Single Responsibility Principle (SRP).
3. Avoiding hardcoded query strings in the service layer and moving them to the repository layer. 
4. Leveraging Spring Data JPA's derived query methods or custom query annotations for better abstraction and reduced boilerplate code.
5. Moving the jdbc connection credentails to a password vault
By adopting a consistent approach, the code base would be easier to understand, extend, and debug in the long term.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
There are pros and cons in both the approaches.
Using an OpenAPI YAML file to generate code has the following pros and cons:

**Pros:**
1. Consistency: Generated code ensures uniformity in API structure, reducing the chances of human error.
2. Faster Development: Automates repetitive tasks, saving time when creating boilerplate code.
3. Documentation: The OpenAPI spec doubles as documentation, making it easier for developers and consumers to understand the API.
4. Validation: Tools can validate the spec for correctness, ensuring compliance with standards.
5. Ease of Updates: Changes to the API can be made in the spec and regenerated, ensuring consistency across the codebase.

**Cons:**
1. Complexity: Requires understanding and maintaining the OpenAPI spec, which can be challenging for small teams or simple APIs.
2. Customization Limitations: Generated code may require manual adjustments for specific use cases, leading to potential maintenance overhead.
3. Dependency on Tools: Relies on third-party tools for code generation, which may introduce compatibility issues or limitations.

For directly coding endpoints:

**Pros:**
1. Flexibility: Developers have full control over the implementation, allowing for custom logic and optimizations.
2. No Tool Dependency: Avoids reliance on external tools, reducing potential compatibility issues.
3. Simplicity: Easier for small teams or projects with minimal API complexity.

**Cons:**
1. Inconsistency: Manually written code may lead to variations in style and structure across endpoints.
2. Time-Consuming: Writing boilerplate code for each endpoint can be repetitive and slow.
3. Lack of Documentation: Without a spec, there may be less formal documentation, making it harder for others to understand the API.

**Choice:**
For a project with multiple APIs like this one, I would recommend standardizing on the OpenAPI approach for all endpoints. 
This ensures consistency, improves maintainability, and provides clear documentation. 
However, for smaller or less complex APIs, direct coding might be acceptable if the team prefers simplicity and flexibility.
```