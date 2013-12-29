#!/usr/bin/env python

# Various pieces of Esperanto grammar/word construction/etc. in the form of a
# Parcon railroad diagram.

import sys

from parcon.railroad import Then, Or, Token, Loop, Bullet, Nothing
from parcon.railroad import PRODUCTION, TEXT, ANYCASE, DESCRIPTION
from parcon.railroad.raildraw import draw_to_svg

text = lambda t: Token(TEXT, t)
description = lambda t: Token(DESCRIPTION, t)
optional = lambda t: Or(t, Nothing())

productions = {
    "word": Then(
        Bullet(),
        description("root"),
        Or(
            text("e"),
            text("i"),
            Then(
                Or(
                    text("a"),
                    text("o")
                ),
                optional(text("j")),
                optional(text("n"))
            )
        ),
        Bullet()
    )
}

draw_to_svg(productions, {}, sys.argv[1], True)
