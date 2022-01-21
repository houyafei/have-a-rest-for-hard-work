# -*- coding:utf-8 -*-
# python3

import requests as r
from bs4 import BeautifulSoup

def getBeautifulSentence():
    url='https://tool.lu/tip/'
    hd={
    'Accept': '*/*',
    'Connection': 'keep-alive',
    'User-agent': 'Mozilla/5.0 (Windows NT 10.0; WOW 64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36 QIHU 360SE'
    }

    resp = r.get(url, headers=hd)
    if resp.status_code == 200:
        soup = BeautifulSoup(resp.text,'html.parser')

        contents = soup.find_all('div',class_='note-container')
        # 输出
        for content in contents:
            return content.get_text().strip()
    else:
        return 'Who knows the real?'

if __name__ == '__main__':
    print(getBeautifulSentence())
