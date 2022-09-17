./gradlew clean build installDist
rsync -avP -essh build/install/ ithome-updater:./ithome-ironman-2022/
