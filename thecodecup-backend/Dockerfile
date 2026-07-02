# Builder stage
FROM python:3.13-slim AS builder

COPY --from=ghcr.io/astral-sh/uv:latest /uv /uvx /bin/

WORKDIR /app

# Copy ONLY the dependency definition files first to leverage Docker layer caching
COPY pyproject.toml uv.lock ./

# Install dependencies into a virtual environment (.venv)
# --frozen: Ensure exact versions from uv.lock are installed (reproducible build)
# --no-dev: Exclude development and testing packages
# --no-install-project: Skip installing the source code itself to optimize cache layers
RUN uv sync --frozen --no-dev --no-install-project


# Final stage
FROM python:3.13-slim

WORKDIR /app

# Copy the pre-built virtual environment from the 'builder' stage
# This leaves all build tools and caches behind, keeping the final image tiny
COPY --from=builder /app/.venv /app/.venv

# Prepend the virtual environment's bin directory to the system PATH
# This ensures that Python and FastAPI commands automatically use the isolated environment
ENV PATH="/app/.venv/bin:$PATH"

COPY . /app

CMD ["fastapi", "run", "app/app.py", "--host", "0.0.0.0", "--port", "8000"]