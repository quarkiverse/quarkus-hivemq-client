# Session Management - User-Specific Sessions

## Overview
This directory contains session files for tracking development work by different developers. Each developer has their own folder named after their Git username (with spaces replaced by underscores) to organize sessions and avoid conflicts.

## Directory Structure

### User Folders
```
{git_username_normalized}/
```
Each developer gets a dedicated folder based on their Git user.name (spaces converted to underscores).

**Examples**:
- Git name: `pablo gonzalez granados` → Folder: `pablo_gonzalez_granados/`
- Git name: `alice` → Folder: `alice/`
- Git name: `Bob Smith` → Folder: `Bob_Smith/`

### Files Within User Folder

#### Active Session
```
{username}/session-current.md
```
The active development session for the user. This file is continuously updated during development work.

**Example**: `pablo_gonzalez_granados/session-current.md`

#### Archived Sessions
```
{username}/session-{number}.md
```
Completed sessions are archived with incrementing numbers. These provide historical context and project continuity.

**Examples**:
- `pablo_gonzalez_granados/session-001.md`
- `pablo_gonzalez_granados/session-002.md`
- `alice/session-001.md`
- `Bob_Smith/session-003.md`

#### Personal Template (Optional)
```
{username}/session-template.md
```
Personal session template for a specific user (optional).

**Example**: `pablo_gonzalez_granados/session-template.md`

## Workflow

### Starting a New Session
1. Framework auto-creates your user folder (based on git config user.name)
2. Creates or uses `{your_folder}/session-current.md`
3. Initialize session with project context, objectives, and tasks
4. Work through tasks, updating the session file as you progress

### Completing a Session
1. Mark all tasks as completed in the session file
2. Archive the session by renaming to `session-{next-number}.md` in your folder
3. Create a new `session-current.md` for the next work session

### Finding Your Sessions
All your sessions are in your dedicated folder:
```bash
# List your folder
ls -la pablo_gonzalez_granados/

# View your current session
cat pablo_gonzalez_granados/session-current.md

# View all your archived sessions
ls -1 pablo_gonzalez_granados/session-*.md | grep -v current

# View all developers' folders
ls -d */
```

## Benefits

### Multi-User Collaboration
- **No Conflicts**: Each developer has a separate folder
- **Clear Ownership**: Folder name shows who owns the sessions
- **Git Friendly**: Folders can be committed without merge conflicts
- **Parallel Work**: Multiple developers can work simultaneously
- **Clean Organization**: Sessions grouped by developer, not scattered with prefixes

### Session Continuity
- **Historical Context**: Archived sessions provide project history per developer
- **Knowledge Sharing**: Team members can review each other's session folders
- **Audit Trail**: Complete record of who did what and when
- **Easy Navigation**: Simple folder structure, no prefix confusion

### Framework Integration
The Claude Code framework automatically:
- Detects your Git user.name from git config
- Normalizes the name (replaces spaces with underscores)
- Creates your user folder if it doesn't exist
- Creates session files without prefixes inside your folder
- Archives completed sessions with proper numbering
- Maintains session continuity across work periods

## Example Directory Structure

```
.claude/tasks/
├── README.md                               # This file
├── session-template.md                     # Global template (optional)
├── .gitignore                              # Git ignore rules
├── pablo_gonzalez_granados/                # Pablo's folder
│   ├── session-current.md                  # Active session
│   ├── session-001.md                      # Archived session 1
│   └── session-002.md                      # Archived session 2
├── alice/                                  # Alice's folder
│   ├── session-current.md                  # Active session
│   └── session-001.md                      # Archived session 1
└── Bob_Smith/                              # Bob's folder
    ├── session-current.md                  # Active session
    ├── session-001.md                      # Archived session 1
    └── session-002.md                      # Archived session 2
```

## Git Integration

### Recommended .gitignore Pattern
If you want to keep session files private:
```gitignore
# Keep all active sessions private
*/session-current.md

# Or keep entire user folders private
pablo_gonzalez_granados/
alice/

# Or use wildcards to ignore all user folders except shared ones
*/
!shared/
```

### Sharing Sessions
If you want to share your sessions with the team:
```bash
# Commit your completed sessions
git add .claude/tasks/pablo_gonzalez_granados/session-*.md
git commit -m "docs: Add session history for {feature_name}"

# Or commit your entire folder
git add .claude/tasks/pablo_gonzalez_granados/
git commit -m "docs: Share Pablo's development sessions"
```

## Automated Session Management

The master-orchestrator agent handles:
- Automatic user folder creation (normalized Git username)
- Session initialization without filename prefixes
- Real-time session updates during development
- Session archival when work is complete
- Session numbering and naming consistency within user folders
- Cross-session context preservation

## Best Practices

1. **Regular Updates**: Keep your session file updated as you work
2. **Clear Objectives**: Define clear goals at the start of each session
3. **Archive Completed Work**: Don't let old sessions pile up as "current"
4. **Review History**: Periodically review your archived sessions for insights
5. **Share Knowledge**: Consider committing completed sessions for team visibility
6. **Folder Hygiene**: Keep only relevant sessions, clean up old experiments

## Getting Your Git Username

The framework detects your Git user.name and normalizes it:
1. Reads from: `git config user.name`
2. Normalizes: Replaces spaces with underscores
3. Creates folder: `{normalized_name}/`

Current Git user.name: `pablo gonzalez granados`
Normalized folder: `pablo_gonzalez_granados/`

To verify or change your Git username:
```bash
# Check current name
git config user.name

# Set new name (use your actual name, not GitHub username)
git config user.name "Your Full Name"
```

## Username Normalization Rules

The framework applies these rules to create folder names:
- Spaces → Underscores (`" "` → `"_"`)
- All other characters preserved as-is
- Case sensitivity maintained (Linux/Mac) or ignored (Windows)

**Examples**:
- `pablo gonzalez granados` → `pablo_gonzalez_granados`
- `Alice Johnson` → `Alice_Johnson`
- `bob-smith` → `bob-smith` (no change)
- `john.doe` → `john.doe` (no change)

## Support

For issues or questions about session management:
- Check CLAUDE.md for overall framework documentation
- Review .claude/context/control/system-workflows.md for workflow details
- See .claude/agents/master-orchestrator.md for session orchestration logic