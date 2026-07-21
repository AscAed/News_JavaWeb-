## 2025-07-11 - Adding ARIA labels to icon-only buttons
**Learning:** Found multiple instances of icon-only buttons (like search and theme toggles) lacking `aria-label` attributes. This is a crucial accessibility improvement for screen readers.
**Action:** Always check `el-button` components that only contain an `el-icon` (or `icon` prop) to ensure they have an appropriate `aria-label` if they lack text content.

## 2025-07-12 - Adding ARIA labels to buttons inside tooltips
**Learning:** Found an icon-only button wrapped inside an `el-tooltip` that lacked an `aria-label`. While the tooltip provides visual context on hover, it does not provide accessible names for screen readers natively in this implementation.
**Action:** Always ensure icon-only buttons have an `aria-label`, even if they are wrapped in a tooltip component.
## 2025-07-13 - Adding ARIA labels to search buttons
**Learning:** Found an icon-only `el-button` used inside a `template #append` block for a search input that lacked an `aria-label`. The icon alone does not provide sufficient context for screen reader users on its purpose.
**Action:** Always ensure that icon-only buttons used as input add-ons or appendages have an explicit `aria-label` describing their action.
