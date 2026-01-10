---
name: /commit-msg
id: commit-msg
category: Git
description: 根据当前改动自动生成中文 commit message（可选给出一键提交命令）
---
**目标**
基于最近开发的改动，生成一条中文 commit message（优先使用 `feat|fix|docs|chore|refactor` 前缀），必要时给出一条可直接执行的提交命令。

**输入**
- 默认使用当前工作区改动（优先 staged；若无 staged，则用 unstaged）
- 若用户指定范围（文件/目录/功能点），以用户指定为准

**步骤**
1. 执行并阅读：
   - `git status --porcelain`
   - `git diff --name-only --staged` 与 `git diff --staged`
   - 若 staged 为空：改用 `git diff --name-only` 与 `git diff`
   - `git log -5 --oneline`
2. 提炼“本次提交的单一主题”：
   - 优先选变更最多/最核心的功能点（接口、模块、能力）
   - 若包含多主题：建议拆分，或仅为主主题生成 message 并提示用户拆分
3. 选择类型前缀：
   - 新增功能/接口：`feat:`
   - 修复问题：`fix:`
   - 只改文档：`docs:`
   - 构建/脚本/配置/杂项：`chore:`
   - 代码重构不改行为：`refactor:`
4. 生成输出（尽量短且可读）：
   - 第一行：只输出一条 commit message（中文 + 关键名词/路径/接口）
   - 若用户需要一键命令：再输出一条 `git add -A && git commit -m "..."`（message 与第一行一致）

**输出要求**
- 默认只输出 commit message 一行（除非用户明确要命令）
- 不输出额外解释、示例或总结
