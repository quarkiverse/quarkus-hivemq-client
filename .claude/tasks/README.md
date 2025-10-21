# Session Management - User-Specific Sessions

## Overview
This directory contains session files for tracking development work by different GitHub users. Each developer has their own session files prefixed with their GitHub username to avoid conflicts and maintain clear ownership.

## File Naming Convention

### Active Session
```
{github_username}_session-current.md
```
The active development session for the user. This file is continuously updated during development work.

**Example**: `pagonzal_session-current.md`

### Archived Sessions
```
{github_username}_session-{number}.md
```
Completed sessions are archived with incrementing numbers. These provide historical context and project continuity.

**Examples**:
- `pagonzal_session-001.md`
- `pagonzal_session-002.md`
- `alice_session-001.md`
- `bob_session-003.md`

### Session Template (Optional)
```
{github_username}_session-template.md
```
Personal session template for a specific user.

**Example**: `pagonzal_session-template.md`

## Workflow

### Starting a New Session
1. Create or use your `{username}_session-current.md` file
2. Initialize session with project context, objectives, and tasks
3. Work through tasks, updating the session file as you progress

### Completing a Session
1. Mark all tasks as completed in the session file
2. Archive the session by renaming to `{username}_session-{next-number}.md`
3. Create a new `{username}_session-current.md` for the next work session

### Finding Your Sessions
All your sessions will be prefixed with your GitHub username:
```bash
# List all your sessions
ls -1 {your_username}_*.md

# View your current session
cat {your_username}_session-current.md

# View your archived sessions
ls -1 {your_username}_session-*.md | grep -v current
```

## Benefits

### Multi-User Collaboration
- **No Conflicts**: Each developer has separate session files
- **Clear Ownership**: Username prefix shows who owns each session
- **Git Friendly**: Files can be committed without merge conflicts
- **Parallel Work**: Multiple developers can work simultaneously

### Session Continuity
- **Historical Context**: Archived sessions provide project history
- **Knowledge Sharing**: Team members can review each other's sessions
- **Audit Trail**: Complete record of who did what and when

### Framework Integration
The Claude Code framework automatically:
- Detects your GitHub username from git config
- Creates appropriately named session files
- Archives completed sessions with proper numbering
- Maintains session continuity across work periods

## Example Directory Structure

```
.claude/tasks/
├── README.md                          # This file
├── session-template.md                # Global template (optional)
├── pagonzal_session-current.md        # Pablo's active session
├── pagonzal_session-001.md            # Pablo's completed session 1
├── pagonzal_session-002.md            # Pablo's completed session 2
├── alice_session-current.md           # Alice's active session
├── alice_session-001.md               # Alice's completed session 1
└── bob_session-current.md             # Bob's active session
```

## Git Integration

### Recommended .gitignore Pattern
If you want to keep session files private:
```gitignore
# Keep personal sessions private
.claude/tasks/*_session-current.md

# Or keep all personal sessions private
.claude/tasks/{your_username}_*.md
```

### Sharing Sessions
If you want to share your sessions with the team:
```bash
# Commit your completed sessions
git add .claude/tasks/{your_username}_session-*.md
git commit -m "docs: Add session history for {feature_name}"
```

## Automated Session Management

The master-orchestrator agent handles:
- Automatic session initialization with username prefix
- Real-time session updates during development
- Session archival when work is complete
- Session numbering and naming consistency
- Cross-session context preservation

## Best Practices

1. **Regular Updates**: Keep your session file updated as you work
2. **Clear Objectives**: Define clear goals at the start of each session
3. **Archive Completed Work**: Don't let old sessions pile up as "current"
4. **Review History**: Periodically review your archived sessions for insights
5. **Share Knowledge**: Consider committing completed sessions for team visibility

## Getting Your GitHub Username

The framework attempts to detect your GitHub username from:
1. Git config user.name
2. Git config user.email (username part)
3. Home directory name
4. Manual input if auto-detection fails

To set your GitHub username explicitly:
```bash
git config user.name "your-github-username"
```

## Support

For issues or questions about session management:
- Check CLAUDE.md for overall framework documentation
- Review .claude/context/control/system-workflows.md for workflow details
- See .claude/agents/master-orchestrator.md for session orchestration logic