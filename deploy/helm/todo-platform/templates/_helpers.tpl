{{- define "todo-platform.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "todo-platform.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- include "todo-platform.name" . -}}
{{- end -}}
{{- end -}}

{{- define "todo-platform.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" -}}
{{- end -}}

{{- define "todo-platform.commonLabels" -}}
helm.sh/chart: {{ include "todo-platform.chart" . }}
app.kubernetes.io/name: {{ include "todo-platform.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end -}}

{{- define "todo-platform.selectorLabels" -}}
app.kubernetes.io/name: {{ include "todo-platform.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}

{{- define "todo-platform.postgresSecretName" -}}
{{- default (printf "%s-postgres" (include "todo-platform.fullname" .)) .Values.postgres.auth.passwordSecretName -}}
{{- end -}}
