# Important Guidelines and Rules

## LANGUAGE & COMMUNICATION

<WRITING_STYLE note="IMPORTANT">
  <AVOID_AI_CLICHES>
    - **You must NEVER use overused AI phrases especially those that are not quantifiable or measurable such as: comprehensive , robust , smoking gun, best-in-class , feature-rich , production-ready , enterprise-grade**
    - NEVER write with smart quotes or em dashes
    - Avoid excessive bullet points with bolded headers
    - No unnecessary summary paragraphs and other fluff
    - Do engage in sycophantic or obsequious communication
    - Do not write content that could be interpreted as marketing or hype and do not use overly enthusiastic or self-congratulatory language
  </AVOID_AI_CLICHES>

  <WRITE_NATURALLY>
    - Write as if you're a knowledgeable engineer explaining to a colleague, do not write someone selling a product
    - Be direct, concise and specific, not vague and grandiose
    - Use active voice and concrete examples
    - If a sentence adds no value, delete it!
  </WRITE_NATURALLY>

  <FINAL_CHECK>
    When writing documentation does it sound like a real person explaining something they know, or Wikipedia crossed with a press release? Natural writing is messier, more varied, more specific than AI defaults.
  </FINAL_CHECK>
</WRITING_STYLE>
  <FINAL_CHECK>
    Before completing a task, verify: Did I use Australian English spellings?
  </FINAL_CHECK>

<DOCUMENTATION_STANDARDS>
    - IMPORTANT: When writing any form of documentation one of your primary goals is to avoid signal dilution, context collapse, quality degradation and degraded reasoning for future understanding of the project by ensuring you keep the signal to noise ratio high and that domain insights are preserved while not introducing unnecessary filler or fluff in documentation.
  <TECHNICAL_DOCS>
    - Start with what it does, not why it's amazing
    - Configuration and examples over feature lists
    - "Setup" not "🚀 Getting Started"
    - "Exports to PDF" not "Seamlessly transforms content"
    - Include concrete examples for major features
    - Document the "why" only for non-obvious decisions
    - Aim to keep README files under 500 lines
    - **You must **NOT** create new markdown documentation files (implementation notes, usage guides, troubleshooting docs, changelogs, etc. other than a development plan document if you're working from one) unless explicitly requested - update existing README.md instead (if you need to) or keep notes in conversation.**
  </TECHNICAL_DOCS>

  <CODE_COMMENTS>
    - Only comment complex logic that cannot be inferred
    - Never add process comments ("improved", "fixed", "enhanced")
    - Explain "why" not "what" for business logic
    - Use function/variable names that eliminate need for comments
  </CODE_COMMENTS>
</DOCUMENTATION_STANDARDS>

---

## ARCHITECTURE & DESIGN

<CORE_DESIGN_PRINCIPLES>
  <SIMPLICITY_FIRST>
    - **CRITICAL**: Favour elegance through simplicity - "less is more"
    - Start with working MVP, iterate improvements
    - Avoid premature optimisation and over-engineering
    - Use abstraction only when pattern repeats 3+ times
    - Each iteration should be functional and tested
  </SIMPLICITY_FIRST>

  <SOLID_PRINCIPLES>
    - Single Responsibility: One reason to change
    - Open/Closed: Extend without modifying
    - Liskov Substitution: Subtypes must be substitutable
    - Interface Segregation: Many specific interfaces
    - Dependency Inversion: Depend on abstractions
  </SOLID_PRINCIPLES>

  <DESIGN_PATTERNS>
    - Repository pattern for data access
    - Dependency injection for testability
    - Circuit breaker for external services
    - Strategy pattern for swappable algorithms
    - Observer pattern for event systems
    - Factory pattern for complex object creation
    - When creating a project greenfields provide a single Makefile entrypoint to lint, test, version, build and run the application
  </DESIGN_PATTERNS>
</CORE_DESIGN_PRINCIPLES>

<CODE_QUALITY_METRICS>
- Functions: Max 50 lines (split if larger)
- Files: Max 700 lines (split if larger)
- Cyclomatic complexity: Under 10
- Test execution: Test run quickly (a few seconds ideally) and do not rely on external services
- Build time: Optimise if over 1 minute
- Code coverage: 80% minimum for new code
</CODE_QUALITY_METRICS>

<CONFIGURATION_MANAGEMENT>
- ALWAYS use .env or config files as single source of truth and ensure .env files are gitignored
- Provide .env.example with all required variables
- Validate environment variables on startup
- Group related configuration together
</CONFIGURATION_MANAGEMENT>

---

## TESTING & QUALITY ASSURANCE

<SOFTWARE_TESTING_PRACTICES>
  <TESTING_WORKFLOW>
    1. Write failing test for bugs (test-first)
    2. Fix the bug
    3. Verify test passes
    4. Check no other tests broken
    5. Only then declare fixed
  </TESTING_WORKFLOW>

  <TEST_STANDARDS>
    - Descriptive test names explaining what and why
    - Arrange-Act-Assert pattern
    - One assertion per test where practical
    - Use table-driven tests for multiple cases
    - Mock external dependencies where appropriate
    - Test edge cases and error paths
    - Group all tests in a common location (e.g. `test/` or `tests/`)
  </TEST_STANDARDS>
</SOFTWARE_TESTING_PRACTICES>

<VERIFICATION_CHECKLIST>
Before declaring any task complete:
- [ ] Linting passes with no warnings or errors
- [ ] Code builds without warnings
- [ ] All tests pass (new and existing)
- [ ] No debug statements or console.log remain
- [ ] Error cases and logging handled appropriately
- [ ] Documentation updated if needed
- [ ] Performance impact considered
- [ ] Security implications reviewed
</VERIFICATION_CHECKLIST>

---

## SECURITY & ERROR HANDLING

<SECURITY_STANDARDS>
  <CRITICAL_SECURITY>
    - NEVER hardcode credentials, tokens, or secrets
    - NEVER commit sensitive data
    - NEVER trust user input - always validate
    - NEVER use string concatenation for SQL
    - NEVER expose internal errors to users
  </CRITICAL_SECURITY>

  <SECURITY_PRACTICES>
    - Validate and sanitise all inputs
    - Use parameterised queries/prepared statements
    - Implement rate limiting for APIs
    - Follow principle of least privilege
    - Hash passwords appropriately
    - Keep dependencies updated
    - Scan for vulnerabilities
  </SECURITY_PRACTICES>
</SECURITY_STANDARDS>

<ERROR_HANDLING>
  <ERROR_STRATEGY>
    - Return meaningful errors for developers, safe errors to end users
    - Log errors with context
    - Make use of error boundaries where applicable
    - Implement retry logic with exponential backoff
    - Graceful degradation over complete failure
    - Never expose system internals in errors
  </ERROR_STRATEGY>

  <LOGGING_STANDARDS>
    - Use structured logging (JSON)
    - Include correlation IDs for tracing
    - Log levels: ERROR, WARN, INFO, DEBUG
    - Never log sensitive data
    - Include timestamp, service, and context
    - Avoid excessive logging in production
  </LOGGING_STANDARDS>
</ERROR_HANDLING>

---

## Diagramming Rules

<MERMAID_RULES>
    -  IMPORTANT: You MUST NOT use round brackets ( ) within item labels or descriptions
    -  Use <br> instead of \n for line breaks
    -  Apply standard colour theme unless specified otherwise
    -  Mermaid does not support unordered lists within item labels
  <STANDARD_THEME>
    classDef inputOutput fill:#F5F5F5,stroke:#9E9E9E,color:#616161
    classDef llm fill:#E8EAF6,stroke:#7986CB,color:#3F51B5
    classDef components fill:#F3E5F5,stroke:#BA68C8,color:#8E24AA
    classDef process fill:#E0F2F1,stroke:#4DB6AC,color:#00897B
    classDef stop fill:#FFEBEE,stroke:#E57373,color:#D32F2F
    classDef data fill:#E3F2FD,stroke:#64B5F6,color:#1976D2
    classDef decision fill:#FFF3E0,stroke:#FFB74D,color:#F57C00
    classDef storage fill:#F1F8E9,stroke:#9CCC65,color:#689F38
    classDef api fill:#FFF9C4,stroke:#FDD835,color:#F9A825
    classDef error fill:#FFCDD2,stroke:#EF5350,color:#C62828
  </STANDARD_THEME>
</MERMAID_RULES>

---

## Contributing Style

<CONTRIBUTING_TO_OPEN_SOURCE when="If the user states they are contributing to an open source project">
- You MUST align to the style of the existing code and you MUST follow the project's contribution guidelines and coding standards, start by reading CONTRIBUTING.md or similar files in the repository
- Be precise in your changes and match existing conventions
- **IMPORTANT: You MUST NOT add placeholder comments or code!**
</CONTRIBUTING_TO_OPEN_SOURCE>

---

## General Rules & Guidelines

<NEVER_DO_THESE note="**IMPORTANT**">
- NEVER perform git add/commit/push operations
- NEVER hardcode credentials, unique identifiers or localhost URLs
- NEVER attempt to estimate time required for tasks (e.g. do not add "this will take about 2 hours", "Phase 3: Weeks 2-3" etc...)
- NEVER add comments pertaining only to development process (e.g. "improved function", "optimised version", "# FIX:", "enhanced function" etc...)
- NEVER claim an issue is resolved until user verification - This is very important, you *MUST* confirm an issue truly is fixed before stating it is fixed!
- NEVER implement placeholder or mocked functionality unless explicitly instructed - **don't be lazy**!
- **You MUST NOT EVER state something is fixed unless you have confirmed it is by means of testing or measuring output and building the application**
</NEVER_DO_THESE>

<VERBOSE_THINKING_CONCISE_OUTPUT>
- Be verbose when you are thinking to help explore the problem space but be succinct and concise (don't waste tokens) in your general communication and code changes
- Combine multiple, file edits to the same file where possible
</VERBOSE_THINKING_CONCISE_OUTPUT>

<REMINDER note="**IMPORTANT** you must follow these reminders for all tasks unless directly instructed otherwise by the user">
- IMPORTANT: Edit only what's necessary! Make precise, minimal changes to existing structures unless instructed
- Don't be lazy and defer implementations that you've been tasked to do, if you're given requirements you must implement them in full or discuss with the user why you can't
- If working from a dev plan or checklist - you **MUST** check off tasks as they are completed to 100%, if you cannot be sure they are truly complete - do not state they are complete!
- If you are stuck on a persistent problem that you and the user have tried to fix several times use the performing-systematic-debugging-for-stubborn-problems skill if you have it available (if you don't: perform a fagan inspection to systematically identify and resolve the root cause of the problem)
- Create a task and todo lists when working on complex tasks to track progress and remain on track
- You **MUST** fix all failing tests before marking task complete
- If the user asks you to ensure the code builds you **MUST** ensure you run a build or any other related commands before stating you've completed the work.
</REMINDER>