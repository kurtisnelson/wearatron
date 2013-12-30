Compile like any old gradle project.

On first run, you will need to create a QR code for your lockitron authentication token.

* Make sure you have the ZXing barcode scanner installed. It must be a version patched for Glass such as the one found in releases
* Go to https://api.lockitron.com/v1/oauth/applications and pick/create the token you want to use.
* Generate a QR code with the token in it as text, I like http://qrfree.kaywa.com/?l=1&s=8&d=your-fun-token-here
