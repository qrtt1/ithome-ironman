./gradlew clean installDist
rsync -avP -essh build/install/ bot:./ithome-ironman-2021/
