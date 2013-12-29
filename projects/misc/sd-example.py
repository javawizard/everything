#!/usr/bin/env python

# Run as one of:
# python sd-example.py png out.png
# python sd-example.py svg out.svg

# Syntax diagram of the example grammar given at
# http://en.wikipedia.org/w/index.php?title=Syntax_diagram&oldid=584596058

import sys
from collections import OrderedDict

from parcon.railroad import Then, Or, Token, Loop, Bullet, Nothing
from parcon.railroad import PRODUCTION, TEXT
from parcon.railroad.raildraw import draw_to_image

production = lambda t: Token(PRODUCTION, t)
text = lambda t: Token(TEXT, t)

productions = OrderedDict([
    (
        "expression",
        Then(
            Bullet(),
            Loop(
                production("term"),
                text("+")
            ),
            Bullet()
        )
    ),
    (
        "term",
        Then(
            Bullet(),
            Loop(
                production("factor"),
                text("*")
            ),
            Bullet()
        )
    ),
    (
        "factor",
        Then(
            Bullet(),
            Or(
                production("constant"),
                production("variable"),
                Then(
                    text("("),
                    production("expression"),
                    text(")")
                )
            ),
            Bullet()
        )
    ),
    (
        "variable",
        Then(
            Bullet(),
            Or(
                text("x"),
                text("y"),
                text("z")
            ),
            Bullet()
        )
    ),
    (
        "constant",
        Then(
            Bullet(),
            Loop(
                production("digit"),
                Nothing()
            ),
            Bullet()
        )
    ),
    (
        "digit",
        Then(
            Bullet(),
            Or(
                text("0"),
                text("1"),
                text("2"),
                text("3"),
                text("4"),
                text("5"),
                text("6"),
                text("7"),
                text("8"),
                text("9")
            ),
            Bullet()
        )
    )
])

options = {
    "raildraw_title_before":20,
    "raildraw_title_after": 30,
    "raildraw_scale": 0.7
}

draw_to_image(sys.argv[1], productions, options, sys.argv[2], True)











