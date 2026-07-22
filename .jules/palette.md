## 2025-07-11 - Adding ARIA labels to icon-only buttons
**Learning:** Found multiple instances of icon-only buttons (like search and theme toggles) lacking `aria-label` attributes. This is a crucial accessibility improvement for screen readers.
**Action:** Always check `el-button` components that only contain an `el-icon` (or `icon` prop) to ensure they have an appropriate `aria-label` if they lack text content.

## 2025-07-12 - Adding ARIA labels to buttons inside tooltips
**Learning:** Found an icon-only button wrapped inside an `el-tooltip` that lacked an `aria-label`. While the tooltip provides visual context on hover, it does not provide accessible names for screen readers natively in this implementation.
**Action:** Always ensure icon-only buttons have an `aria-label`, even if they are wrapped in a tooltip component.
## $(date +%Y-%m-%d) - Add aria-label to search button
**Learning:** Found an icon-only button without an `aria-label` attribute in the Vue components (`NewsList.vue`), which causes accessibility issues as screen readers won't announce its purpose correctly.
**Action:** Added `aria-label="搜索"` to improve accessibility for screen readers on icon-only interactive elements.
