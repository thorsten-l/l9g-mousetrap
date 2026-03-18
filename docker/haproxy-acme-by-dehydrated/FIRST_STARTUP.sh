rm -fr acme certs dehydrated dehydrated-data private run
mkdir -p certs dehydrated-data run
docker compose run --rm -e DEHYDRATED_COMMAND=register dehydrated
docker compose run --rm -e DEHYDRATED_COMMAND=issue dehydrated
docker compose up -d
docker compose logs -f 
