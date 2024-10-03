# Tips
## Only run some tests
```bash
gradle test --tests "*SearXNG*" -x :frontend:pnpmBuild -x :backend:copyWebApp
```