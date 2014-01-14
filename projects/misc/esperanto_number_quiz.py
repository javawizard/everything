# coding=UTF-8

import random
import time

NUMBERS = ["nul", "unu", "du", "tri", "kvar", "kvin", "ses", "sep", "ok",
    u"naŭ"]
GROUPS = [[], ["mil"], ["miliono"], ["miliardo"], ["duiliono"], ["duiliardo"]]


def convert_single(n, suffix=""):
    if n == 0:
        return []
    elif n == 1 and suffix:
        return [suffix]
    else:
        return [NUMBERS[n] + suffix]


def convert_group(n, suffix=[]):
    if not n:
        return []
    if n == 1 and suffix:
        return suffix
    hundreds = n / 100
    n = n % 100
    tens = n / 10
    n = n % 10
    return convert_single(hundreds, "cent") + convert_single(tens, "dek") + convert_single(n) + suffix


def convert_number(n):
    if n == 0:
        return NUMBERS[0]
    result = []
    for g in GROUPS:
        value = n % 1000
        n = n / 1000
        result = convert_group(value, g) + result
    if n:
        raise ValueError("Numbers that large aren't yet supported")
    return " ".join(result)


def generate_single(p0, p1):
    r = random.random()
    if r < p0:
        return 0
    elif r < p0 + p1:
        return 1
    else:
        return random.randrange(2, 10)


def generate_number(p0, p1, positions=3):
    n = 0
    for _ in range(positions):
        n = n * 10
        n = n + generate_single(p0, p1)
    return n


def quiz(iterations=10, words=True, p0=0.4, p1=0.3, positions=3):
    start = time.time()
    for _ in range(iterations):
        n = generate_number(p0, p1, positions)
        s = convert_number(n)
        n = str(n)
        if words:
            o, i = s, n
        else:
            o, i = n, s
        print o
        while True:
            test = raw_input()
            if not test:
                print "Answer: " + i
                break
            if test.replace("ux", "ŭ") == i:
                break
            print "Nope, try again."
    end = time.time()
    print str(end - start) + " seconds overall"
    print str((end - start) / iterations) + " per capita"


if __name__ == "__main__":
    quiz()

























