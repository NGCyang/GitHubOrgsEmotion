pos = []
neg = []
neut = []
with open('subjclueslen1-HLTEMNLP05.tff', 'r') as file:
    for line in file:
        print(line)
        splitline = line.split()
        print(len(splitline))
        wordsec =  splitline[2].split("=")
        word = wordsec[1]
        print(len(wordsec))
        polaritysec = splitline[5].split("=")
        polarity = polaritysec[1]
        print(len(polaritysec))
        if polarity == "positive":
            pos.append(word)
        elif polarity == "negative":
            neg.append(word)
        elif polarity == "neutral":
            neut.append(word)

outfile = open('emotionDict_V2','w+')
for word in pos:
    print(word)
    outfile.write(word)
    outfile.write(',')
outfile.write('\n')
for word in neg:
    outfile.write(word)
    outfile.write(',')
outfile.write('\n')
for word in neut:
    outfile.write(word)
    outfile.write(',')
'''
outfile.write(pos)
outfile.write(neg)
outfile.write(neut)
'''
outfile.close()

