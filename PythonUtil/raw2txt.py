#Convert raw surface data into tables

import os
from time import sleep

DATA_FOLDER = "../../ftp.ncdc.noaa.gov/pub/data/noaa"
ISH_CLASSPATH = "../../ftp.ncdc.noaa.gov"

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
        ISH_CLASSPATH,ffile,ffile+'.txt'))
      print("Re-compressing file...")
      os.system('gzip --best {}'.format(ffile+'.txt'))
      sleep(1) #Be nice

if __name__=='__main__':
  print("I'm main!")
  main()
