#Convert raw surface data into tables

import os
from time import sleep

DATA_FOLDER = "../../ftp.ncdc.noaa.gov/pub/data/noaa"

def main():
  for sdir, dirs, files in os.walk(DATA_FOLDER):
    for file in files:
      if not '.gz' in file:
        continue
      ffile = os.path.join(sdir, file)
      print("Processing {}...".format(file))
      print("Unzipping {}...".format(file))
      os.system('gunzip ' + ffile)
      print("Formatting {} into table...".format(file))
      os.system('java -classpath {} ishJava {} {}'.format(
        DATA_FOLDER,ffile.replace('.gz',''),ffile.replace('.gz','')
        +'.txt'))
      print("Re-compressing file...")
      os.system('gzip --best {}'.format(ffile+'.txt'))
      sleep(100) #Be nice

if __name__=='__main__':
  print("I'm main!")
  main()
