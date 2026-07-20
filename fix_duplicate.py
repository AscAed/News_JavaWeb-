import re

with open("News_Vue/src/api/modules/interaction.ts", "r") as f:
    lines = f.readlines()

new_lines = []
for line in lines:
    if line.strip() == "import type { ApiResponse } from '@/types/headline'":
        continue
    new_lines.append(line)

with open("News_Vue/src/api/modules/interaction.ts", "w") as f:
    f.writelines(new_lines)
