import uvicorn

if __name__ == "__main__":
    
    print("Starting server at http://0.0.0.0:8000...")
    print("Swagger UI: http://0.0.0.0:8000/docs")
    uvicorn.run("app.app:app", host="0.0.0.0", port=8000, reload=True)