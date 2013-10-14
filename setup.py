from setuptools import setup

setup(
    name="stm-console",
    version="0.1.1",
    description="A transactional variant of remote-stm",
    author="Alexander Boyd",
    author_email="alex@opengroove.org",
    py_modules=["stm_console"],
    install_requires=["stm", "remote-console"]
)
