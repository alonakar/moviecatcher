#!/bin/bash
set -e

# --- Config ---
DATE_TAG="05_13_2025"
MOVIE_FILE="movie_ids_${DATE_TAG}.json"
TV_FILE="tv_series_ids_${DATE_TAG}.json"
TRANSFORMED_FILE="media_transformed.json"
BULK_FILE="bulk_media_en.json"
ES_HOST="http://localhost:9200"
INDEX_NAME="media_en"

echo "▶ Cleaning up old files..."
rm -f "$TRANSFORMED_FILE" "$BULK_FILE" part_*

# --- Step 1: Transform movies without UID ---
echo "▶ Transforming movies"
jq -c '
  select(.id) |
  {
    original_title: .original_title,
    overview: .overview,
    popularity: .popularity,
    posterUrl: .posterUrl,
    type: "movie",
    cast: .cast,
    external_ids: {tmdb: .id}
  }
' "$MOVIE_FILE" > movies.tmp

# --- Step 2: Transform TV without UID ---
echo "▶ Transforming TV series"
jq -c '
  select(.id) |
  {
    original_title: (.original_title // .original_name),
    overview: .overview,
    popularity: .popularity,
    posterUrl: .posterUrl,
    type: "tv",
    cast: .cast,
    external_ids: {tmdb: .id}
  }
' "$TV_FILE" > tv.tmp

# --- Step 3: Merge and assign UID ---
echo "▶ Merging and generating unique UIDs"
cat movies.tmp tv.tmp | awk '{ print }' | nl -n rz -w 8 | awk '
  BEGIN { FS="\t" }
  {
    uid = sprintf("uid-%08d", $1);
    print $2 | "jq -c --arg uid \"" uid "\" \047. + { uid: $uid }\047"
  }
' > "$TRANSFORMED_FILE"

# --- Clean temp files ---
rm movies.tmp tv.tmp

# --- Step 4: Convert to Elasticsearch bulk format ---
echo "▶ Converting to Elasticsearch bulk format"
jq -c --arg index "$INDEX_NAME" '
  . as $doc |
  { index: { _index: $index, _id: $doc.uid } },
  $doc
' "$TRANSFORMED_FILE" > "$BULK_FILE"

# --- Step 5: Split for bulk upload ---
echo "▶ Splitting bulk file into chunks of 10,000 lines"
split -l 10000 "$BULK_FILE" part_


echo "✅ Done: All media processed, bulk formatted, and ready to be uploaded."
