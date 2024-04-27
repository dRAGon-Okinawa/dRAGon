#!/bin/bash

# mkdocs 1.5.x doesn't play well with some **TEXTS**
pip install mkdocs=="1.4.3"
pip install mkdocs-material=="9.0.4"
pip install mkdocs-git-revision-date-localized-plugin
pip install mkdocs-redirects