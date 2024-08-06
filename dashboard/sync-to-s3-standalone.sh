yarn build:prod_root
aws s3 --profile alan sync build/ s3://ironman-2024.qrtt1.io/ --delete --exclude "ui-data.json" --exclude "data.json"
