---
trigger: model_decision
description: Create a development plan
---

# Development Plan Creation

You have been working with the user to understand their goal. Now you will create a structured development plan using a development plan & checklist workflow that can guide development work systematically.

## Your Task

**Step 0: Scope Assessment**

Before creating a plan, you MUST assess whether this work requires a formal development plan and what tier of plan is appropriate.

**Assessment Criteria:**

Ask yourself these questions:

1. **Complexity indicators** - Does this change:
   - Affect multiple subsystems or cross architectural boundaries?
   - Require breaking changes or API modifications?
   - Impact backwards compatibility or existing integrations?
   - Involve complex business logic or security-critical code?
   - Require changes to data models or database schema?

2. **Scope indicators** - Is this:
   - A single-file change with obvious solution?
   - A focused feature within one subsystem?
   - A multi-system feature requiring coordination?
   - An architectural change affecting core patterns?

3. **Scale indicators** - Count the concrete work:
   - How many files will likely be touched? (1-2, 3-10, >10)
   - How many new tests will be needed? (None, few, comprehensive suite)
   - How many subsystems are affected? (1, 2-3, >3)
   - How many integration points need updating? (None, few, many)

**Decision Tree:**

- **No plan needed**: Single file, <5 tasks, obvious solution → Just do the work
- **Micro plan** (target <80 lines): Bug fix, dependency update, simple refactor within single subsystem → Use simplified template
- **Standard plan** (target 100-250 lines): Focused feature, single subsystem with tests, moderate complexity → Full template, 2-4 phases
- **Complex plan** (target 250-400 lines): Multi-system feature, architectural changes, high complexity → Full template, 4-6 phases
- **Too large** (>400 lines projected): Consider splitting into multiple plans or questioning scope with user

**Escalation triggers** - STOP and discuss with user if:
- Involves architectural decisions beyond current scope
- Dependencies on unavailable resources
- Unclear complex requirements that need significant clarification
- Fundamental approach or technology choices are uncertain

Once you've assessed the scope, proceed with the appropriate level of planning.

**Step 1: Gather Context (if working with existing code)**

If there is existing code in the project that may be relevant to the goal and that you haven't already analysed:
1. Read relevant files in the project directory
2. Examine existing documentation (README.md, docs/, CONTRIBUTING.md, etc.)
3. Analyse codebase structure, dependencies, and package versions
4. Identify coding conventions and patterns currently used
5. Review existing tests to understand expected behaviour and testing patterns

**Step 2: Deep Thinking**

Use ultrathink (think deeply) about your understanding of the user's requirements, context, constraints and goal based on your discussion so far and any code/documentation you've reviewed.

Consider:
- What is the actual problem being solved? (Not just symptoms)
- What assumptions am I making that should be validated?
- Are there simpler approaches I haven't considered?
- What could go wrong during implementation?
- Does the proposed solution align with existing architecture?

**Step 3: Create the Plan**

Create a development plan (in `DEVELOPMENT_PLAN.md` unless specified otherwise) that SHALL:
1. Concisely and clearly document what needs to be done and why
2. Break work into checklists within logical, reviewable phases
3. Provide enough guidance without over-constraining implementation
4. Set measurable, objective success criteria
5. Be executable by an AI coding agent in a fresh session without further context other than any existing files in the project
6. Include a "Working Notes" section where the executing agent can track complex issues and troubleshooting attempts
7. Use specification-style language (MUST/SHALL/SHOULD) for requirements and constraints

## Plan Structure

Your plan MUST include these essential components (adjust detail level based on scope tier):

### 1. Overview & Current State

**Overview:**
- What problem are you solving or feature are you building?
- Why does this need to be done? (Business value, technical debt, bug fix, etc.)
- Brief summary of the approach (1-3 sentences)

**Current State:**
- What's the current situation?
- What specific problems or limitations exist?
- Relevant technical context (tech stack, architecture, existing patterns)
- What have you already investigated or learned during discussion?

### 2. Requirements & Constraints

Use specification-style language to make requirements unambiguous:

**Functional Requirements** (What the solution MUST do):
- Use "MUST" for mandatory requirements
- Use "SHOULD" for strongly recommended but not mandatory
- Use "MAY" for optional features
- Example: "The API MUST return 400 errors for invalid input"
- Example: "Error messages SHOULD include field-level validation details"

**Technical Constraints** (What MUST be respected):
- Architectural patterns that MUST be followed
- Compatibility requirements (e.g., "MUST maintain backwards compatibility with v2.x API")
- Performance requirements (e.g., "Response time MUST be <200ms for 95th percentile")
- Security requirements (e.g., "User input MUST be sanitised before database queries")

**Exclusions** (What MUST NOT change):
- Existing APIs that MUST remain unchanged
- Dependencies that MUST NOT be upgraded
- Patterns or approaches that MUST be avoided

**Prerequisites**:
- Dependencies that MUST be available
- Configuration that MUST be in place
- Access or permissions required

**Make every requirement specific, testable, and unambiguous.** Avoid vague statements like "should be robust" or "must be user-friendly".

### 3. Success Criteria

Define clear, concise, objective, measurable criteria for completion:

✅ **Good success criteria, e.g:**
- "All TypeScript compilation errors resolved"
- "Test coverage >80% for new error handling code"
- "API response time <200ms for all endpoints"
- "User can register, log in, and access protected routes"
- "Build succeeds with zero warnings or errors"
- "All existing tests pass"

❌ **Avoid vague criteria, e.g:**
- "Robust/comprehensive/enterprise-grade solution"
- "Good user experience"

**Include quality gates:**
- Linting must pass without warnings or errors
- All tests must pass locally without depending on external services
- Build must succeed

### 4. Development Plan (Phased Checklist)

Break the work into **2-6 logical phases** that:
- Contain a checklist of tasks (and subtasks if required)
- Follow a clear progression (lint -> build → test → critical self review & fixes -> human review and approval)

**For each phase:**
- Give it a clear, concise and descriptive name
- List 3-8 specific tasks (if more, break into sub-phases)
- Describe **outcomes**, not specific code changes
- Include verification/testing steps
- Add a "Perform a critical self-review of your changes and fix any issues found" task

## The Goldilocks Principle

Tasks should describe **what** needs to be achieved, not **how** to implement it:

❌ **Too Vague:**
- "Improve the API"
- "Fix performance issues"
- "Add better error handling"

❌ **Too Prescriptive:**
- "In api.ts line 45, change `if (x)` to `if (x && y)`"
- "Create file called UserService.ts with methods getUser(), createUser(), deleteUser()"
- "Use exactly this code: [code snippet]"

✅ **Just Right:**
- "Add input validation to all API endpoints, returning 400 errors for invalid requests"
- "Add null checking to API handlers to prevent runtime errors"
- "Implement centralised error handling middleware for consistent error responses"

**The sweet spot:** Specific enough that an agent knows what success looks like, flexible enough that the agent can determine the best implementation approach.

## Phase Design Guidelines

### Good Phase Structure

Each phase should:

1. **Have a clear purpose**
   - ✅ "Phase 1: Foundation & Dependencies"
   - ❌ "Phase 1: Various updates"

2. **Deliver reviewable value**
   - ✅ "Update dependencies and verify build succeeds"
   - ❌ "Update package.json" (can't verify it works yet)

3. **Follow logical order**
   - e.g. Setup → Core functionality → Error handling → Testing → Review

4. **Include verification**
   - ✅ "Run tests to verify error handling works correctly"
   - ✅ "Build application and confirm no errors or warnings"
   - ❌ Just listing tasks without verification

5. **End with review checkpoint**
   - Every phase must include a task: `Perform a critical self-review of your changes and fix any issues found`
   - Every phase must end: `- [ ] STOP and wait for human review`

### Phase Size

- **Too small:** 1-2 trivial tasks per phase (overhead of constant review)
- **Too large:** >10 tasks in a phase (break into sub-phases)
- **Just right:** 3-8 tasks that together achieve a coherent milestone

## Critical Self-Review

Before presenting the plan to the user, you MUST perform a critical self-review:

1. **Alignment check**: Does this plan actually solve the user's stated problem? Am I solving symptoms or root causes?

2. **Scope check**: Is the scope realistic and focused, or have I crept beyond the original goal? Does it match my initial scope assessment?

3. **Complexity check**:
   - Does this need a dev plan at all, or is it simple enough to just do?
   - Is the plan tier (micro/standard/complex) appropriate for the work?
   - Am I over-engineering or under-specifying?

4. **Clarity check**: Could a different AI agent read this plan in a fresh session and know exactly what to do? Are requirements unambiguous?

5. **Assumptions check**: Are there hidden assumptions that should be documented in the Unknowns & Assumptions section?

6. **Trade-offs check**: Have I considered alternative approaches? Is this the most proportionate solution?

7. **Red flags check**: Review the "Red Flags to Avoid" section - does your plan have any of these issues?

8. **Design-critic check** (RECOMMENDED): Consider performing a quick design-critic style review your plan before user presentation:
   - Challenge architectural decisions
   - Identify over-engineering or complexity
   - Question technology choices
   - Validate that requirements match the stated problem

If you identify issues during this review, you MUST revise the plan before presenting it to the user.

## Important Reminders

- **Focus on outcomes, not implementations** - Let the executing agent make good technical decisions
- **Be specific but flexible** - Clear success criteria, flexible approach
- **Avoid over-engineering** - Solve the actual problem proportionately
- **Break into reviewable chunks** - Phases should deliver standalone value
- **Testing throughout** - Not just at the end
- **Use the user's context** - Reference specific files, technologies, and constraints they've requested
- **Be concise** - Avoid fluff, don't waste tokens

Remember: A good plan is clear enough to guide systematic work, but flexible enough to allow the executing agent to make smart decisions about implementation details.