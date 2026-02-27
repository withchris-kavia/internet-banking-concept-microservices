"""Minimal entrypoint for local Python checks.

This repository is primarily a Java/Spring Boot microservices monorepo.
This file exists to support lightweight Python tooling (lint/static checks) that
may be run by CI or automation in this workspace.
"""


# PUBLIC_INTERFACE
def function() -> None:
    """Print a small greeting used for sanity checks."""
    print("hello world")


if __name__ == "__main__":
    function()
