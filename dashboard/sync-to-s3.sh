yarn build:prod
aws s3 --profile alan sync build/ s3://ithome-ironman-watcher/2024/ --acl public-read --delete --exclude "ui-data.json" --exclude "data.json"
