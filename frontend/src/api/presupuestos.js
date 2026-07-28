const BASE = '/api/presupuestos';

/**
 * Procesa las respuestas del backend.
 * Permite leer mensajes de error con esta estructura:
 *
 * {
 *   "message": "Mensaje enviado por el backend"
 * }
 */
async function procesarRespuesta(response) {
  if (response.status === 204) {
    return true;
  }

  let data = null;

  try {
    data = await response.json();
  } catch {
    data = null;
  }

  if (!response.ok) {
    const mensaje =
      data?.message ||
      data?.error ||
      'Ocurrió un error al procesar la solicitud.';

    throw new Error(mensaje);
  }

  return data;
}

/**
 * Obtiene todos los presupuestos.
 */
export async function getPresupuestos() {
  const response = await fetch(BASE);

  return procesarRespuesta(response);
}

/**
 * Obtiene un presupuesto por su identificador.
 */
export async function getPresupuesto(id) {
  const response = await fetch(`${BASE}/${id}`);

  return procesarRespuesta(response);
}

/**
 * Obtiene el presupuesto asociado a una orden.
 */
export async function getPresupuestoByOrden(ordenId) {
  const response = await fetch(
    `${BASE}/orden/${ordenId}`
  );

  return procesarRespuesta(response);
}

/**
 * Crea un presupuesto.
 */
export async function createPresupuesto(data) {
  const response = await fetch(BASE, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });

  return procesarRespuesta(response);
}

/**
 * Acepta un presupuesto pendiente.
 *
 * Se mantiene la ruta /aprobar porque así está
 * definida en el controlador del backend.
 */
export async function aprobarPresupuesto(id) {
  const response = await fetch(
    `${BASE}/${id}/aprobar`,
    {
      method: 'PUT',
    }
  );

  return procesarRespuesta(response);
}

/**
 * Rechaza un presupuesto pendiente.
 */
export async function rechazarPresupuesto(id) {
  const response = await fetch(
    `${BASE}/${id}/rechazar`,
    {
      method: 'PUT',
    }
  );

  return procesarRespuesta(response);
}

/**
 * Elimina un presupuesto.
 */
export async function deletePresupuesto(id) {
  const response = await fetch(
    `${BASE}/${id}`,
    {
      method: 'DELETE',
    }
  );

  return procesarRespuesta(response);
}