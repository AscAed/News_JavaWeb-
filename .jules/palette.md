## 2025-07-11 - Adding ARIA labels to icon-only buttons
**Learning:** Found multiple instances of icon-only buttons (like search and theme toggles) lacking `aria-label` attributes. This is a crucial accessibility improvement for screen readers.
**Action:** Always check `el-button` components that only contain an `el-icon` (or `icon` prop) to ensure they have an appropriate `aria-label` if they lack text content.

## 2025-07-12 - Adding ARIA labels to buttons inside tooltips
**Learning:** Found an icon-only button wrapped inside an `el-tooltip` that lacked an `aria-label`. While the tooltip provides visual context on hover, it does not provide accessible names for screen readers natively in this implementation.
**Action:** Always ensure icon-only buttons have an `aria-label`, even if they are wrapped in a tooltip component.

## 2024-07-20 - [Make Card Components Keyboard Accessible]
**Learning:** A common anti-pattern in Vue applications is adding `cursor: pointer` and `@click` handlers to internal elements (like titles or images) of a card, while ignoring the root container. This makes the card inaccessible to keyboard users and provides a poor click target for mouse users.
**Action:** When creating clickable cards in Vue, ensure the root container has `@click`, `@keydown.enter.prevent`, `@keydown.space.prevent`, `tabindex="0"`, a meaningful `role` (like `article`, `button`, or `link`), an appropriate `aria-label`, and a clear `:focus-visible` outline.
