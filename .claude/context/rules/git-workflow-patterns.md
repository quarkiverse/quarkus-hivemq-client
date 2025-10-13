---
description: Git workflow protocols and branch management patterns
alwaysApply: true
---

# Git Workflow Patterns

## Git Protocol Standards
**CRITICAL**: Read and follow these protocols before ANY git operations:

### Branch Management
- `main` - production branch. Never push without thorough testing.
- `dev` - staging/development environment
- Feature branches: `feature/description`
- Personal branches: `developer-name/feature`

### Push Protocol (EXACT ORDER - SEPARATE TERMINALS)
```bash
# Terminal 1: Checkout and pull
git checkout BRANCH && git pull origin BRANCH

# Terminal 2: Add and commit  
git add . && git commit -m "concise yet complete message"

# Terminal 3: Push
git push origin BRANCH
```

### Merge Protocol (EXACT ORDER - SEPARATE TERMINALS)
```bash
# Terminal 1: Checkout target and pull
git checkout TARGET && git pull origin TARGET

# Terminal 2: Merge source branch
git merge SOURCE

# Terminal 3: Push merged changes
git push origin TARGET
```

### Critical Git Rules
- **NEVER PUSH** unless user explicitly asks
- **NO FORCE PUSH** without explicit permission
- **NO NEWLINES** in commit messages
- **ALWAYS INCLUDE** commit message with commits
- **NEVER RUN** `git clean -fdx` (destroys project files)
- **ASK USER** to run build commands, don't run them yourself

### 🔄 Automated Commit Protocol
**MANDATORY**: Create automated commits when TodoWrite tasks complete:

```bash
# Trigger: All TodoWrite tasks marked complete
# Step 1: Validate changes
git status  # Check modified files

# Step 2: Run pre-commit checks (if available)
npm run lint && npm run typecheck  # Or equivalent commands

# Step 3: Stage all task-related changes
git add [files modified during task execution]

# Step 4: Create descriptive commit
git commit -m "feat: [task summary] - Auto-commit after completing [N] tasks 🤖"

# Step 5: Notify user
echo "✅ Auto-commit created after completing all tasks"
```

**Commit Message Examples**:
- `feat: implement user profile page - Auto-commit after completing 5 tasks 🤖`
- `fix: resolve authentication issues - Auto-commit after completing 3 tasks 🤖`
- `refactor: optimize database queries - Auto-commit after completing 4 tasks 🤖`

**Auto-Commit Rules**:
- Only commit when ALL TodoWrite tasks are complete
- Include meaningful task summary in commit message
- Stage only files modified during task execution
- Run validation before committing (lint, typecheck)
- NEVER push automatically - only commit
- Notify user immediately after commit

### Pull Protocol
```bash
# Terminal 1: Check status
git status

# Terminal 2: Pull changes
git pull origin BRANCH
```

### Repository Structure Awareness
- Check project structure before running commands
- Navigate to correct folder in monorepos
- Verify correct directory for Next.js build commands
- Respect .gitignore and project organization

---

Follow these git workflow protocols consistently to ensure safe and effective version control management.