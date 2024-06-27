#!/bin/bash

echo "Setting up pnpm store..."
mkdir -p /tmp/pnpm && pnpm config set store-dir /tmp/pnpm/

echo "Building the project..."
gradle pnpmInstall build