#!/usr/bin/python

import autocomplete

autocomplete.load()

text = ""
textSplit = []
while True:
    s = raw_input()
    text = text + s
    textSplit = text.split(" ")[-2:]
    print "looking for predictions of", textSplit
    try:
        print autocomplete.predict(*textSplit)
    except Exception:
        print "it failed..."
