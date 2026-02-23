#!/usr/bin/env zsh

# Maven version (change if needed)
MAVEN_VERSION=3.9.11
MAVEN_DIR="$HOME/tools"
MAVEN_HOME="$MAVEN_DIR/apache-maven-$MAVEN_VERSION"

echo "Installing Apache Maven $MAVEN_VERSION..."

# Create tools directory
mkdir -p $MAVEN_DIR
cd $MAVEN_DIR || exit

# Download Maven
wget https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz

# Extract
tar -xzf apache-maven-$MAVEN_VERSION-bin.tar.gz

# Remove archive
rm apache-maven-$MAVEN_VERSION-bin.tar.gz

# Update .zshrc if not already added
if ! grep -q "MAVEN_HOME" ~/.zshrc; then
  echo "" >> ~/.zshrc
  echo "# Maven configuration" >> ~/.zshrc
  echo "export MAVEN_HOME=$MAVEN_HOME" >> ~/.zshrc
  echo 'export PATH=$MAVEN_HOME/bin:$PATH' >> ~/.zshrc
fi

echo "Installation complete!"
echo "Run: source ~/.zshrc"
echo "Then verify with: mvn -version"