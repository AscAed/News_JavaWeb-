## 2025-07-11 - Adding ARIA labels to icon-only buttons
**Learning:** Found multiple instances of icon-only buttons (like search and theme toggles) lacking `aria-label` attributes. This is a crucial accessibility improvement for screen readers.
**Action:** Always check `el-button` components that only contain an `el-icon` (or `icon` prop) to ensure they have an appropriate `aria-label` if they lack text content.

## 2025-07-12 - Adding ARIA labels to buttons inside tooltips
**Learning:** Found an icon-only button wrapped inside an `el-tooltip` that lacked an `aria-label`. While the tooltip provides visual context on hover, it does not provide accessible names for screen readers natively in this implementation.
**Action:** Always ensure icon-only buttons have an `aria-label`, even if they are wrapped in a tooltip component.

## $(date +%Y-%m-%d) - Adding ARIA labels to icon-only buttons in input appendages
**Learning:** Found an icon-only button inside an `el-input` appendage (`<template #append>`) lacking an `aria-label`. Screen readers need this to understand the button's purpose since there's no visible text.
**Action:** Ensure icon-only buttons used as input append elements (like search or submit buttons) have a clear `aria-label` describing their action.
