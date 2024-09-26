BLACK='\033[0;30m'
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m'

printf "${CYAN}Let's go for a dRAGon ride! 🐉${NC}\n"
printf "\n"

printf "${YELLOW}[QUICKSTART]${NC}\n"
printf "\t${BLUE}✨ Launch full dRAGon app ${NC}\tgradle bootRun\n"
printf "\t${BLUE}Launch dRAGon backend only${NC}\tgradle bootRun -x :frontend:pnpmBuild -x :backend:copyWebApp\n"
printf "\t${BLUE}Launch dRAGon frontend only${NC}\tpnpm --prefix frontend dev\n"

printf "\n"