yarn build:prod

aws s3 --profile alan sync build/ s3://ithome-ironman-watcher/2022/ --acl public-read --delete --exclude "data.json"
