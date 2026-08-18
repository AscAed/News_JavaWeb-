## 2025-07-11 - Adding ARIA labels to icon-only buttons
**Learning:** Found multiple instances of icon-only buttons (like search and theme toggles) lacking `aria-label` attributes. This is a crucial accessibility improvement for screen readers.
**Action:** Always check `el-button` components that only contain an `el-icon` (or `icon` prop) to ensure they have an appropriate `aria-label` if they lack text content.

## 2025-07-12 - Adding ARIA labels to buttons inside tooltips
**Learning:** Found an icon-only button wrapped inside an `el-tooltip` that lacked an `aria-label`. While the tooltip provides visual context on hover, it does not provide accessible names for screen readers natively in this implementation.
**Action:** Always ensure icon-only buttons have an `aria-label`, even if they are wrapped in a tooltip component.

## 2025-08-18 - Adding ARIA labels to inputs missing explicit labels in Element Plus forms
**Learning:** Found that `el-input` fields in login/register forms lacked accessible names because they relied on `placeholder` attributes rather than having explicit `label` elements or `label` props on their wrapping `el-form-item`. This creates accessibility issues for screen reader users as placeholder text may not always be read correctly as the accessible name for an input.
**Action:** Always ensure that `el-input` components have an `aria-label` attribute if they do not have a corresponding explicit `<label>` element, especially in forms where placeholders are used as the primary visual label.
