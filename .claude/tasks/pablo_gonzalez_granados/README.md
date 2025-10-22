# Session Folder: pablo_gonzalez_granados

## Developer Information
- **Git User Name**: pablo gonzalez granados
- **Folder**: pablo_gonzalez_granados/
- **Normalized From**: "pablo gonzalez granados" (spaces → underscores)

## Session Files

### Active Session
- **File**: `session-current.md`
- **Purpose**: Tracks ongoing development work
- **Status**: Updated continuously during active work
- **Git**: Ignored by default (see ../.gitignore)

### Archived Sessions
- **Pattern**: `session-{number}.md`
- **Purpose**: Historical record of completed development work
- **Numbering**: Sequential (001, 002, 003, ...)
- **Git**: Can be committed for team visibility

## Workflow

### Starting Work
1. Claude framework auto-detects your Git user.name
2. Normalizes to folder name (spaces → underscores)
3. Creates/updates `session-current.md` in this folder
4. You work through tasks as documented in the session

### Completing Work
1. All tasks marked complete in `session-current.md`
2. Session archived as `session-{next-number}.md`
3. Next session starts with fresh `session-current.md`
4. Git commit created automatically (if configured)

## Git Integration

### Default Behavior
- `session-current.md` is gitignored (private, work-in-progress)
- Archived sessions (`session-*.md`) can be committed
- This README can be committed for documentation

### Sharing Sessions
To share completed work with the team:
```bash
# Commit specific session
git add .claude/tasks/pablo_gonzalez_granados/session-001.md
git commit -m "docs: Add session history for feature X"

# Or commit all archived sessions
git add .claude/tasks/pablo_gonzalez_granados/session-*.md
git commit -m "docs: Share development history"

# Or commit entire folder
git add .claude/tasks/pablo_gonzalez_granados/
git commit -m "docs: Share pablo's session folder"
```

## Navigation

### View Your Sessions
```bash
# List all sessions in this folder
ls -la

# View current session
cat session-current.md

# View specific archived session
cat session-001.md

# Count archived sessions
ls -1 session-*.md 2>/dev/null | grep -v current | wc -l
```

### Search Sessions
```bash
# Search for specific content across all sessions
grep -r "search term" .

# Find sessions mentioning a specific file
grep -l "src/main/java/SomeClass.java" session-*.md
```

## Session Template

Sessions in this folder follow the global template at:
- `../session-template.md`

Or you can create a personal template:
- `session-template.md` (in this folder)

## Best Practices

1. **Keep sessions focused**: One major feature or fix per session
2. **Update regularly**: Don't let session-current.md get stale
3. **Archive promptly**: Move completed work to numbered sessions
4. **Document decisions**: Capture architectural decisions and rationale
5. **Share knowledge**: Consider committing completed sessions for the team

## Folder Statistics

This section can be updated manually or automatically:

### Session Count
- Active: 1 (session-current.md)
- Archived: 0 (as of folder creation)
- Total: 1

### Coverage Period
- First Session: TBD
- Latest Session: TBD
- Active Period: TBD

---

**Created**: 2025-10-21
**Last Updated**: 2025-10-21
**Framework**: Claude Code Session Management (Folder-Based)