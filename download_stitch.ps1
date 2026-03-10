$urls = @(
    @{ name="splash.png"; url="https://lh3.googleusercontent.com/aida/AOfcidVUA-DvgOOVKIlNQ7NaaTwSzo6EN2UGasZpBG8gexR1N9UtqPsLM6kH4AGK3Qq1TnyAznYjAMbPzST3jlDgcuKfv5vjQXGne6CkXdFH7qGTGu5jUw1hUhuQkbi2UvvQzXc7zquYOmocsMxGzMOP-FOLzNI0Gc0u1Ge5aL-iTTJ8KoXl43KLQ0XQXqylfmuLLi6XNx6aRnXY1XnD3z8Hck3jQTx69r87_A12s3DShZh7TH2HbHgdaQTQBro" },
    @{ name="splash.html"; url="https://contribution.usercontent.google.com/download?c=CgthaWRhX2NvZGVmeBJ7Eh1hcHBfY29tcGFuaW9uX2dlbmVyYXRlZF9maWxlcxpaCiVodG1sXzI3NGRhZTgxY2Y4MjRkZWI5OWM5OGRmYWNhOTQxZGQyEgsSBxDwnvrilwMYAZIBIwoKcHJvamVjdF9pZBIVQhM0OTAzNTE4ODkyMTU0NTY1OTA2&filename=&opi=89354086" },
    @{ name="login.png"; url="https://lh3.googleusercontent.com/aida/AOfcidVN8zlAVf7nCCMq5ylmzMFjqQmUF5sAJ6FM38EVkEeMCh5TuIBeub3pSYvGEpsuyTH_saJ_Xal00EbCJO0v27COCooV_F87JoB_0jCMkwoTwpMk9M2DHbiy0SWx1SE34j4t4gDZ7aIYdTvyPZ_ToXiBtqkWcrHTEoE3gjOEcUAyGJ2cv3I5aic7uDWtHNSLsrP16_NS5-5POwQWXg1fTjieE5nYCw8fnpcqgySyKV-VrDW0ka7iUz3Tq8Q" },
    @{ name="login.html"; url="https://contribution.usercontent.google.com/download?c=CgthaWRhX2NvZGVmeBJ7Eh1hcHBfY29tcGFuaW9uX2dlbmVyYXRlZF9maWxlcxpaCiVodG1sXzJmNmU2NmY1MjUzZjQ1Y2JhN2E2ZmM1MmVjNWI0NjM1EgsSBxDwnvrilwMYAZIBIwoKcHJvamVjdF9pZBIVQhM0OTAzNTE4ODkyMTU0NTY1OTA2&filename=&opi=89354086" },
    @{ name="home.png"; url="https://lh3.googleusercontent.com/aida/AOfcidWwUrNFSRLR3iXhYyWF9LYj6UFMMizR9Ow7GMmn_ySBYX-MKDMk-Za-GgJmSeSOOTHYKhbHiFks0S-5CvVQMzztq_x8CQ25yiWMvtyyIi1X10vYZFp0TXWBxzneKgyY_-h2NK_JxCCHHnkTlJ72nduy9NsUpd4gxLS8YqQI6XgAX-SKRWkYdw4Zb2OI98FXR_CnuNq3U3WoCVwFhEO6PaZsPUPTSFRelfu2aQiLxfuQmsECIHAgRguXUPw" },
    @{ name="home.html"; url="https://contribution.usercontent.google.com/download?c=CgthaWRhX2NvZGVmeBJ7Eh1hcHBfY29tcGFuaW9uX2dlbmVyYXRlZF9maWxlcxpaCiVodG1sXzI5YmQ0ZGM5MTNjYzRmOThhZGFmMGZiYzRlOGRjYTU1EgsSBxDwnvrilwMYAZIBIwoKcHJvamVjdF9pZBIVQhM0OTAzNTE4ODkyMTU0NTY1OTA2&filename=&opi=89354086" },
    @{ name="upload.png"; url="https://lh3.googleusercontent.com/aida/AOfcidUo-3sSSry1FwcoRuyJ3KAcXKD4eSvDi9-ghoNiExmqAS4Gdz3OCVg5Y1bxR-7FRy4KwiaBJoNwb5JctmoEHN3lA_MaZxbX1P9v-QnTv0wbRNWgCRxX3wxKYkeOm-hksTZKtmttm7yvK8r_Y9DLMfSqtCourEjSBuYa5OP5sM-rZm-6AJWy8WKP1PSwlQOdmhZe7A3aS50HS5oIXGPOvCZ6Ceb0nmWYsDXy-KkFY9fplyeCtepI3PsmrL4" },
    @{ name="upload.html"; url="https://contribution.usercontent.google.com/download?c=CgthaWRhX2NvZGVmeBJ7Eh1hcHBfY29tcGFuaW9uX2dlbmVyYXRlZF9maWxlcxpaCiVodG1sXzMyYzZhNDhhNmRlODRkNTA5NWJlODZmYTJhNGM3MmY4EgsSBxDwnvrilwMYAZIBIwoKcHJvamVjdF9pZBIVQhM0OTAzNTE4ODkyMTU0NTY1OTA2&filename=&opi=89354086" },
    @{ name="ai_processing.png"; url="https://lh3.googleusercontent.com/aida/AOfcidUg4O3tgmRieyRQYQ-2FzBBMARGWsiLs4kgLOiT79EkvVVEadwFDCcSCeDMu_86OWEsmY9Z2EP8SoqFlejBMFwuoZywDKD09WHVKEm48-G_NgsC2lDrp9-CV2454fvcog0FSgPT2s6osyUthycA1TnpqYhg_pHQSjCtfLX4v8SPs1iismnoL-kWfSLEq03tbfQVm-GOLm9UPYI1opPQ5uGf48VQBrwhWUOT9RfJiUWlzyqFgyVaoN6f1Q" },
    @{ name="ai_processing.html"; url="https://contribution.usercontent.google.com/download?c=CgthaWRhX2NvZGVmeBJ7Eh1hcHBfY29tcGFuaW9uX2dlbmVyYXRlZF9maWxlcxpaCiVodG1sXzA1MTM2OWQzZjE1ZjQyNjhiNTNjZGQxN2RmNzRkMTk0EgsSBxDwnvrilwMYAZIBIwoKcHJvamVjdF9pZBIVQhM0OTAzNTE4ODkyMTU0NTY1OTA2&filename=&opi=89354086" }
)

mkdir stitch_ui -ErrorAction SilentlyContinue
foreach ($item in $urls) {
    Write-Host "Downloading $($item.name)..."
    curl.exe -L $item.url -o "stitch_ui\$($item.name)"
}
