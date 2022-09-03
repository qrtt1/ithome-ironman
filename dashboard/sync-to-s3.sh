yarn build

aws s3 --profile alan sync build/ s3://ithome-ironman-watcher/2022/ --exclude "*.json" --acl public-read --delete
