## `metadata.tsv` format
show_uri	show_name	show_description	publisher	language	rss_link	episode_uri	episode_name	episode_description	duration	show_filename_prefix	episode_filename_prefix

foldername = `"show_"show_id`, filename = `episode_id.json`.

* show_uri: `"spotify:show:"foldername(without "show_")` (eg spotify:show:2NYtxEZyYelR6RMKmjfPLB)
* show_filename_prefix: `foldername` (eg show_2NYtxEZyYelR6RMKmjfPLB)
* episode_uri: `"spotify:episode:"filename(without ".json")` (eg spotify:episode:000A9sRBYdVh66csG2qEdj)
* episode_filename_prefix: `filename(without ".json")` (eg 000A9sRBYdVh66csG2qEdj)

## `podcasts_transcripts` format
Folder show_x holds episodes in json format.

## Episode format (`episode_id.json`)
```json
{
    "results": [
        {
            "alternatives": [
                {
                    "transcript": "text text ...",
                    "confidense": 0.7,
                    "words": [
                        {
                            "startTime": "0.0s",
                            "endTime": "0.2s",
                            "word": "word"
                        },
                        ...
                    ]
                }
            ]
        },
        ...
    ]
}
```