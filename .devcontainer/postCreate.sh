#!/bin/bash

# Prepare PNPM
echo "Setting up pnpm store..."
mkdir -p /tmp/pnpm && pnpm config set store-dir /tmp/pnpm/

# Install dependencies
echo "Building the project..."
gradle pnpmInstall build

# Configure the message of the day
echo '/bin/sh .devcontainer/motd.sh' >> /home/vscode/.bashrc

# Let's go for a dRAGon ride! 🐉
printf "${GREEN}Let's go for a dRAGon ride! 🐉\n\n"