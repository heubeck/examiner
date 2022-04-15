FROM quay.io/quarkus/quarkus-distroless-image:1.0
COPY target/*-runner /examiner

EXPOSE 8080
USER nonroot

CMD ["./examiner", "-Dquarkus.http.host=0.0.0.0"]
