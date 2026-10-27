---
name: codebase-learning-guide
description: Create a beginner-friendly illustrated learning guide and developer reference grounded in an existing codebase. Use when a user wants to learn an unfamiliar project through diagrams, guided code tracing, exercises, and verifiable run steps.
---

# Codebase learning guide

Inventory manifests, entry points, source, configuration, schema, tests, and front-end integration. Exclude dependencies, binaries and build artifacts from source coverage. Record what was inspected and what remains unexplored; never equate enumeration with reading every file.

Explain the domain and architecture in plain language first. Trace important workflows through actual UI calls, endpoints, services, database mappings and returned results. Provide source paths and method names; distinguish implemented behavior from comments, assumptions, and runtime observations. Explain dependencies, key terms and why each code layer exists.

Produce a learner-oriented, navigable document with clear chapters, a recommended sequence, examples, concise code excerpts, project-specific architecture/data-flow diagrams and exercises with answer guidance. Include a developer reference: setup prerequisites, commands drawn from the project, relevant tables and endpoints, debugging steps, boundaries and evidence of verification. Avoid exposing credentials. Use real screenshots only when actually captured; label diagrams as illustrations and report missing runtime evidence.

For larger codebases use staged inspection and record scope, exclusions and unknowns. Inspect core paths fully enough to support claims and link out to other modules. Don't manufacture performance metrics, deployment status, ownership, or test results. When a command can alter data or re-execute operations, describe the effect and prefer isolated examples.

Prefer accessible standalone HTML with native diagrams when user requests a visual tutorial and no format is specified; use Markdown if requested. Verify links, diagram rendering and document structure to the extent available. Preserve source code and do not change runtime data solely to obtain illustrative screenshots.
