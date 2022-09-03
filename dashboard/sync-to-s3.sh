yarn build
<<<<<<< Updated upstream
aws s3 sync build/ s3://ithome-2021-ironman --exclude "*.json" --acl public-read --delete
=======
aws s3 sync build/ s3://ithome-ironman-watcher/2022/ --exclude "*.json" --acl public-read --delete
>>>>>>> Stashed changes
