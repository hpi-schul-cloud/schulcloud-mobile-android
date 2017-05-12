# schulcloud-mobile-android
Schul-Cloud Android Application

more to come ..

## Troubleshooting

Problem:

```sh
  Error:Execution failed for task ':app:processDebugGoogleServices'.
  > com.google.gson.stream.MalformedJsonException: Use JsonReader.setLenient(true) to accept malformed JSON at line 1 column 17
```

Solution:

You need to encrypt the gm-secret file with [git-crypt](https://github.com/AGWA/git-crypt). Ask the Schul-Cloud team for the key-file.