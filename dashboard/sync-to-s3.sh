yarn build
aws s3 sync build/ s3://ithome-2022-ironman --exclude "*.json" --acl public-read --delete
