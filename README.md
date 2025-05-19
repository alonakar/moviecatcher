# Movie Catcher

This is a coding task project for movie and tv series search.  

## 🧾 Prepare and Index Media Data into Elasticsearch

---

###  Step 0: Start Elasticsearch

Start a test Elasticsearch node using Docker:

```bash
docker run -d --name elasticsearch \
  -p 9200:9200 \
  -e "discovery.type=single-node" \
  -e "xpack.security.enabled=false" \
  docker.elastic.co/elasticsearch/elasticsearch:8.13.0
 ```

### 🗑Step 1:  Delete Existing Index (Optional)

```bash
curl -X DELETE http://localhost:9200/media_en
 ```

### 🗑Step 2: Create Index with Mapping
Create the media_en index with the appropriate field types and nested structures:

```bash
curl -X PUT "http://localhost:9200/media_en" \
  -H "Content-Type: application/json" \
  -d '{
    "mappings": {
      "properties": {
        "uid": { "type": "keyword" },
        "original_title": { "type": "text" },
        "overview": { "type": "text" },
        "popularity": { "type": "float" },
        "type": { "type": "keyword" },
        "external_ids": {
          "properties": {
            "tmdb": { "type": "keyword", "index": false }
          }
        },
        "cast": {
          "type": "nested",
          "properties": {
            "name": { "type": "text" }
          }
        }
      }
    }
  }'

 ```

### Step 3: Prepare and Transform TMDB Export Data

This job assumes a daily routine where raw TMDB exports are transformed and merged into a localized index (e.g. media_en).
Simplified for demo purposes

✅ 1. Download TMDB daily exports
Download from:
🔗 https://developer.themoviedb.org/docs/daily-id-exports

Example files:
 - movie_ids_05_13_2025.json
 - tv_series_ids_05_13_2025.json

✅ 2. Run transformation script
Run the provided Bash script to handle all preparation steps:

```bash
./prepare_media_index.sh
 ```
###  Step 4: Upload to Elasticsearch

Use this loop to bulk upload the partitioned files:

```bash
for file in part_*; do
echo "Uploading $file"
curl --silent --output /dev/null -X POST "http://localhost:9200/_bulk" \
-H "Content-Type: application/x-ndjson" \
--data-binary "@$file"
done
```

###  Step 5: Verify Your Index
```bash
curl "http://localhost:9200/media_en/_search?q=original_title:harry&pretty"
```

### Notes
 - uid is a pseudo-unique ID used for consistent Elasticsearch document _id
 - output index is media_en — extendable for multiple locales (e.g. media_fr, media_nl)